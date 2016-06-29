
SET NAMESPACE 'http://teiid.org/rest' AS REST;

CREATE VIRTUAL PROCEDURE getProduct (id string(4000)) RETURNS TABLE (NAME string(60))
	AS
BEGIN
	SELECT Products.PRODUCTDATA.NAME FROM Products.PRODUCTDATA WHERE procedureReturnsViewModel.getProduct.id = Products.PRODUCTDATA.INSTR_ID;
END;

CREATE VIRTUAL PROCEDURE selfProc (stringIN string(4000)) RETURNS TABLE (result string(30))
	AS
BEGIN
	SELECT procedureReturnsViewModel.selfProc.stringIN AS result;
END;
