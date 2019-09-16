-- 1.  每日流水号自动从0开始递增 , 
-- 2. 前缀+日期+递增数字+后缀
-- 3. 入参：前缀，后缀,类型    返回值：流水号
-- 4. 如果是每日的第一个： 就将serial_val 变成1从这开始
--    如果是全局中就第一个，就将serial_val新插入为1

-- DROP PROCEDURE generate_serial_val ;
CREATE PROCEDURE `generate_serial_val` (
	IN serialPrefix VARCHAR (20),
	IN serialSuffixes VARCHAR (20),
	IN serialType int(11),
	OUT servialNum VARCHAR (50)
)
BEGIN
-- 当前日期,有可能包含时分秒   
DECLARE currentDate VARCHAR (15);
 -- 查看全局数量
DECLARE globalNum INT  DEFAULT 0 ;
 -- 查看今天的数量
DECLARE firstNum INT  DEFAULT 0; 
-- 计数器的值
DECLARE serialVal INT  DEFAULT 0; 
SELECT count(1) INTO globalNum  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType;

   -- 查看是否是今天第一个 是  UPDATE serialVal 为1 否 递增拿出  serialVal 递增 1 
   -- 当前日期 
   SELECT DATE_FORMAT(NOW(), '%Y%m%d') INTO currentDate;
   SELECT count(1) INTO firstNum  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType and gmt_modified>currentDate; 
IF globalNum = 0 THEN
-- 初始化 第一条数据
	     insert into sys_serial (serial_prefix, serial_suffixes, serial_type, serial_val) VALUES (serialPrefix,serialSuffixes,serialType,1);
 ELSEIF firstNum >0 then 
        update  sys_serial set serial_val = serial_val+1    WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType and gmt_modified>currentDate;
        ELSE
        -- 今日第一次生成更新为1 
        update  sys_serial set serial_val =1    WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType ;
END IF;

    

-- 查看当前的serialVal 计数器
SELECT serial_val INTO serialVal  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType;

-- 生成流水号 前缀+日期+4位数字+后缀
SELECT   
    CONCAT(serialPrefix, currentDate,  LPAD(serialVal, 6, '0'),serialSuffixes) INTO servialNum ;
 
END