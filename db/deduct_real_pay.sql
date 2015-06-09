CREATE TABLE
    DEDUCT_REAL_PAY
    (
        id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
        VERSION VARCHAR(7) COMMENT '版本号',
        TRANS_CODE VARCHAR(8) COMMENT '交易码',
        TRANS_DATETIME TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL COMMENT '交易时间',
        SERIAL_NO VARCHAR(16) COMMENT '商户交易流水号',
        MER_ID VARCHAR(15) COMMENT '商户号',
        MER_NAME VARCHAR(64) COMMENT '商户号',
        TRANS_TYPE VARCHAR(4) COMMENT '交易类型 0402-实时代收',
        BIZ_TYPE VARCHAR(4) COMMENT '业务类型，01- 基金 02- 小贷 03- 保险 04- 资管 05- 其他',
        BIZ_OBJ_TYPE VARCHAR(2) COMMENT '00-对公，01-对私',
        PAYER_ACC VARCHAR(32) COMMENT '付款人账号',
        PAYER_NAME VARCHAR(64) COMMENT '付款人名称',
        CARD_TYPE VARCHAR(1) COMMENT '卡折标志：0-借记卡（默认）1-存折2-贷记卡3-公司账号',
        PAYER_BANK_CODE VARCHAR(12) COMMENT '付款行开户行号',
        PAYER_BANK_NAME VARCHAR(50) COMMENT '付款人开户行名',
        PAYER_BANK_NO VARCHAR(8) COMMENT '付款人开户行编码',
        AMT DECIMAL(13,2) DEFAULT '0.00' COMMENT '交易金额',
        CERT_TYPE VARCHAR(2) COMMENT '开户证件类型',
        CERT_NO VARCHAR(20) COMMENT '开户证件号',
        PROV_NO VARCHAR(6) COMMENT '付款省份编码',
        CITY_NO VARCHAR(6) COMMENT '付款城市编码',
        PURPOSE VARCHAR(64) COMMENT '用途说明',
        POSTSCRIPT VARCHAR(100) COMMENT '附言说明',
        EXEC_CODE VARCHAR(6) COMMENT '响应代码',
        EXEC_MSG VARCHAR(128) COMMENT '响应描述',
        PAY_SERIAL_NO VARCHAR(16) COMMENT '金通平台流水号',
        PRIMARY KEY (id)
    )
    ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='实时代收';