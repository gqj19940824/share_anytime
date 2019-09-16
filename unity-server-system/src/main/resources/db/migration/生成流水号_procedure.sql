-- 1.  ÿ����ˮ���Զ���0��ʼ���� , 
-- 2. ǰ׺+����+��������+��׺
-- 3. ��Σ�ǰ׺����׺,����    ����ֵ����ˮ��
-- 4. �����ÿ�յĵ�һ���� �ͽ�serial_val ���1���⿪ʼ
--    �����ȫ���о͵�һ�����ͽ�serial_val�²���Ϊ1

-- DROP PROCEDURE generate_serial_val ;
CREATE PROCEDURE `generate_serial_val` (
	IN serialPrefix VARCHAR (20),
	IN serialSuffixes VARCHAR (20),
	IN serialType int(11),
	OUT servialNum VARCHAR (50)
)
BEGIN
-- ��ǰ����,�п��ܰ���ʱ����   
DECLARE currentDate VARCHAR (15);
 -- �鿴ȫ������
DECLARE globalNum INT  DEFAULT 0 ;
 -- �鿴���������
DECLARE firstNum INT  DEFAULT 0; 
-- ��������ֵ
DECLARE serialVal INT  DEFAULT 0; 
SELECT count(1) INTO globalNum  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType;

   -- �鿴�Ƿ��ǽ����һ�� ��  UPDATE serialVal Ϊ1 �� �����ó�  serialVal ���� 1 
   -- ��ǰ���� 
   SELECT DATE_FORMAT(NOW(), '%Y%m%d') INTO currentDate;
   SELECT count(1) INTO firstNum  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType and gmt_modified>currentDate; 
IF globalNum = 0 THEN
-- ��ʼ�� ��һ������
	     insert into sys_serial (serial_prefix, serial_suffixes, serial_type, serial_val) VALUES (serialPrefix,serialSuffixes,serialType,1);
 ELSEIF firstNum >0 then 
        update  sys_serial set serial_val = serial_val+1    WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType and gmt_modified>currentDate;
        ELSE
        -- ���յ�һ�����ɸ���Ϊ1 
        update  sys_serial set serial_val =1    WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType ;
END IF;

    

-- �鿴��ǰ��serialVal ������
SELECT serial_val INTO serialVal  FROM sys_serial WHERE serial_prefix = serialPrefix AND serial_suffixes = serialSuffixes AND serial_type = serialType;

-- ������ˮ�� ǰ׺+����+4λ����+��׺
SELECT   
    CONCAT(serialPrefix, currentDate,  LPAD(serialVal, 6, '0'),serialSuffixes) INTO servialNum ;
 
END