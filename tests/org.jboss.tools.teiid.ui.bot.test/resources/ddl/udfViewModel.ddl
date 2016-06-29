
CREATE VIRTUAL FUNCTION ConcatNull (stringLeft string(2000), stringRight string(2000)) RETURNS string(4000)
 OPTIONS("FUNCTION-CATEGORY" 'TEST_FUNCTIONS', JAVA_CLASS 'userdefinedfunctions.ConcatNull', JAVA_METHOD 'concatNull')

CREATE VIEW UdfView (
	namebusiness string
) 
AS
	SELECT ConcatNull(Products.PRODUCTDATA.NAME, ConcatNull(': ', Products.PRODUCTDATA.PRIBUSINESS)) AS namebusiness FROM Products.PRODUCTDATA;

