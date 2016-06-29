
SET NAMESPACE 'http://teiid.org/rest' AS REST;

CREATE VIEW ProductsInfo (
	INSTR_ID string(10) NOT NULL,
	SYMBOL_TYPE integer OPTIONS(CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE'),
	SYMBOL string(10) NOT NULL,
	CUSIP string(10),
	NAME string(60),
	TYPE string(15),
	ISSUER string(10),
	EXCHANGE string(10),
	ISDJI boolean NOT NULL OPTIONS(CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE'),
	ISSP500 boolean NOT NULL OPTIONS(CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE'),
	ISNAS100 boolean NOT NULL OPTIONS(CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE'),
	ISAMEXINT boolean NOT NULL OPTIONS(CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE'),
	PRIBUSINESS string(30)
) 
AS
	SELECT
		Products.PRODUCTSYMBOLS.INSTR_ID, Products.PRODUCTSYMBOLS.SYMBOL_TYPE, Products.PRODUCTSYMBOLS.SYMBOL, Products.PRODUCTSYMBOLS.CUSIP, Products.PRODUCTDATA.NAME, Products.PRODUCTDATA.TYPE, Products.PRODUCTDATA.ISSUER, Products.PRODUCTDATA.EXCHANGE, Products.PRODUCTDATA.ISDJI, Products.PRODUCTDATA.ISSP500, Products.PRODUCTDATA.ISNAS100, Products.PRODUCTDATA.ISAMEXINT, Products.PRODUCTDATA.PRIBUSINESS
	FROM
		Products.PRODUCTSYMBOLS, Products.PRODUCTDATA
	WHERE
		Products.PRODUCTSYMBOLS.INSTR_ID = Products.PRODUCTDATA.INSTR_ID ;

CREATE VIRTUAL PROCEDURE getProduct (instr_id string(10)) RETURNS TABLE (result xml)
 OPTIONS("REST:URI" 'product/{instr_id}', "REST:METHOD" 'GET')
	AS
BEGIN
	SELECT XMLELEMENT(NAME Products, XMLAGG(XMLELEMENT(NAME Product, XMLFOREST(procedureRestViewModel.ProductsInfo.INSTR_ID, procedureRestViewModel.ProductsInfo.SYMBOL_TYPE, procedureRestViewModel.ProductsInfo.SYMBOL, procedureRestViewModel.ProductsInfo.CUSIP, procedureRestViewModel.ProductsInfo.NAME, procedureRestViewModel.ProductsInfo.TYPE, procedureRestViewModel.ProductsInfo.ISSUER, procedureRestViewModel.ProductsInfo.EXCHANGE, procedureRestViewModel.ProductsInfo.ISDJI, procedureRestViewModel.ProductsInfo.ISSP500, procedureRestViewModel.ProductsInfo.ISNAS100, procedureRestViewModel.ProductsInfo.ISAMEXINT, procedureRestViewModel.ProductsInfo.PRIBUSINESS)))) AS result FROM procedureRestViewModel.ProductsInfo WHERE procedureRestViewModel.ProductsInfo.INSTR_ID = procedureRestViewModel.getProduct.instr_id;
END;

CREATE VIRTUAL PROCEDURE addProduct (instr_id string(10), symbol string(10), name string(60), isdji boolean, issp500 boolean, isnas100 boolean, isamexint boolean) RETURNS TABLE (expr1 xml)
 OPTIONS("REST:URI" 'product/', "REST:METHOD" 'POST')
	AS
BEGIN
	DECLARE integer VARIABLES.update_count = 0;
	BEGIN
		INSERT INTO procedureRestViewModel.ProductsInfo (procedureRestViewModel.ProductsInfo.INSTR_ID, procedureRestViewModel.ProductsInfo.SYMBOL, procedureRestViewModel.ProductsInfo.NAME, procedureRestViewModel.ProductsInfo.ISDJI, procedureRestViewModel.ProductsInfo.ISSP500, procedureRestViewModel.ProductsInfo.ISNAS100, procedureRestViewModel.ProductsInfo.ISAMEXINT) VALUES (procedureRestViewModel.addProduct.instr_id, procedureRestViewModel.addProduct.symbol, procedureRestViewModel.addProduct.name, procedureRestViewModel.addProduct.isdji, procedureRestViewModel.addProduct.issp500, procedureRestViewModel.addProduct.isnas100, procedureRestViewModel.addProduct.isamexint);
		VARIABLES.update_count = VARIABLES.ROWCOUNT;
		IF(VARIABLES.update_count = 1)
		BEGIN
			SELECT XMLELEMENT(NAME response, 'Operation<undefined>Successful!');
		END
		ELSE
		BEGIN
			SELECT XMLELEMENT(NAME response, 'Operation<undefined>Failed!');
		END
	EXCEPTION e
		BEGIN
			RAISE SQLWARNING e.EXCEPTION;
			SELECT XMLELEMENT(NAME response, 'Operation<undefined>Failed!');
		END
	END
END;

