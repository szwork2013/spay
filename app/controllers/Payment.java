package controllers;

import business.Platform;
import com.shove.security.Encrypt;
import constants.Constants;
import constants.IPSConstants;
import controllers.IPS.IPayment;
import controllers.PNR.ChinaPnrPayment;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import services.IPS;
import utils.ErrorInfo;
import utils.PaymentUtil;

import java.util.Map;

/**
 * 中间件支付控制器入口
 *
 * @author Administrator
 */
public class Payment extends BaseController {

    /**
     * 支付接口主入口
     *
     * @param version    接口版本
     * @param type       接口类型
     * @param memberId   会员id
     * @param memberName 会员名称
     * @param domain     约定密钥
     */
    public static void spay(String version, int type, long memberId, String memberName, String domain) {
        if (StringUtils.isBlank(version)) {
            flash.error("请传入晓风资金托管版本");
            Application.error();
        }

        if (type <= 0) {
            flash.error("传入参数有误");
            Application.error();
        }

        if (StringUtils.isBlank(domain)) {
            Logger.info("domain不允许为空");
            return;
        }

        ErrorInfo error = new ErrorInfo();
        Platform platform = new Platform();
        platform.domain = Encrypt.decrypt3DES(domain, Constants.ENCRYPTION_KEY);

        if (error.code < 0) {
            flash.error(error.msg);
            Application.error();
        }

        Logger.info("------------version = " + version + "\n" + "------------请求的支付平台：" + platform.gatewayId + "\n------------请求的接口：" + type + "--");

        if (Constants.VERSION1.equals(version)) {
            switch (platform.gatewayId) {
                //环迅
                case Constants.IPS: {
                    String argMerCode = params.get("argMerCode");
                    String arg3DesXmlPara = params.get("arg3DesXmlPara");
                    String argSign = params.get("argSign");
                    String argIpsAccount = params.get("argIpsAccount");
                    String argMemo = params.get("argMemo");
                    String flow = params.get("flow");
                    String autoInvest = params.get("autoInvest");
                    String autoPayment = params.get("autoPayment");
                    String wsUrl = params.get("wsUrl");

                    if (type == IPSConstants.TRANSFER || type == IPSConstants.DEDUCT) {
                    /*与使用平台的校验*/
                        if (!PaymentUtil.checkSign(argMerCode, arg3DesXmlPara, argSign)) {
                            Logger.info("------------------------资金托管平台校验失败-------------------------------");
                            flash.error("sign校验失败");
                            Application.error();
                        }

                        String transferInfo = IPS.transfer((int) platform.id, memberId, type, argMerCode, arg3DesXmlPara, error);
                        Logger.info("---------------transferInfo:" + transferInfo + "-----------");
                        renderText(transferInfo);
                    }
				
				/*自动还款*/
                    if (type == IPSConstants.REPAYMENT && IPSConstants.AUTO_PAYMENT.equals(autoPayment)) {
					/*与使用平台的校验*/
                        if (!PaymentUtil.checkSign(argMerCode, arg3DesXmlPara, argSign)) {
                            Logger.info("------------------------资金托管平台校验失败-------------------------------");
                            flash.error("sign校验失败");
                            Application.error();
                        }

                        Map<String, String> args = IPS.entrance(type, (int) platform.id, memberId, memberName, argMerCode, Encrypt.decrypt3DES(arg3DesXmlPara, Constants.ENCRYPTION_KEY), argSign);
                        String autoPaymentInfo = IPS.autoPayment(args, error);

                        if (error.code < 0) {
                            flash.error(error.msg);
                            Application.error();
                        }
                        Logger.info("---------------autoInvestInfo:" + autoPaymentInfo + "-----------");
                        renderText(autoPaymentInfo);
                    }

                    if (platform.useType == 1) {
                        IPayment.ipsTest(type, (int) platform.id, memberId, memberName, argMerCode, arg3DesXmlPara, argSign, argIpsAccount, argMemo);
                    } else {
                        IPayment.ips(platform.domain, type, (int) platform.id, memberId, memberName, argMerCode, arg3DesXmlPara, argSign, argIpsAccount, argMemo, flow, autoInvest, autoPayment, wsUrl);
                    }
                    break;
                }
            }
        } else if (Constants.VERSION2.equals(version)) {
            switch (platform.gatewayId) {
                //汇付
                case Constants.PNR: {
                    String argMerCode = params.get("argMerCode");  //商户号
                    String arg3DesXmlPara = params.get("arg3DesXmlPara");  //xml通过3des加密的参数
                    String argSign = params.get("argSign");  //md5加密之后的校验参数
                    String argIpsAccount = params.get("argIpsAccount");  //第三方客户号
                    String extra = params.get("argeXtraPara");  //xml通过3des加密的参数2, 针对版本2.0添加的所需参数
                    Logger.info("------------>>>>>>>>>>extra " + extra);
                    ChinaPnrPayment chinaPnrPayment = new ChinaPnrPayment();
                    String isWS = params.get("isWS");
                    chinaPnrPayment.pnr(platform.domain, type, (int) platform.id, memberId, memberName, argMerCode, arg3DesXmlPara, extra, argSign, argIpsAccount, isWS);
                    break;
                }

            }
        }
    }
}
