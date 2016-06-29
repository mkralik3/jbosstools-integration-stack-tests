FOR EACH ROW
BEGIN ATOMIC
	DECLARE integer VARIABLES.KOUNT;
	DECLARE integer VARIABLES.ROWS_UPDATED;
	IF(("NEW".INSTR_ID IS NULL) OR ("NEW".ISDJI IS NULL) OR ("NEW".ISSP500 IS NULL) OR ("NEW".ISAMEXINT IS NULL) OR ("NEW".ISNAS100 IS NULL) OR ("NEW".SYMBOL IS NULL) OR ("NEW".NAME IS NULL))
	BEGIN
		RAISE SQLEXCEPTION 'The<undefined>following<undefined>elements<undefined>Instr_ID,<undefined>ISDJI,<undefined>ISSP500,<undefined>ISAMEXINT,<undefined>ISNAS100,<undefined>Symbol,<undefined>and<undefined>Name<undefined>are<undefined>not<undefined>nullable,<undefined>non-null<undefined>values<undefined>should<undefined>be<undefined>inserted<undefined>for<undefined>these<undefined>elements.';
	END
	ELSE
	BEGIN
		VARIABLES.KOUNT = (SELECT COUNT(*) FROM Products.PRODUCTSYMBOLS WHERE Products.PRODUCTSYMBOLS.SYMBOL = "NEW".SYMBOL);
		IF(VARIABLES.KOUNT > 0)
		BEGIN
			RAISE SQLEXCEPTION 'Specified<undefined>symbol<undefined>already<undefined>exists<undefined>in<undefined>database<undefined>-<undefined>Choose<undefined>another<undefined>symbol<undefined>to<undefined>insert.';
		END
		ELSE
		BEGIN
			VARIABLES.KOUNT = (SELECT COUNT(*) FROM Products.PRODUCTDATA WHERE Products.PRODUCTDATA.INSTR_ID = "NEW".INSTR_ID);
			IF(VARIABLES.KOUNT > 0)
			BEGIN
				RAISE SQLEXCEPTION 'Specified<undefined>instrument<undefined>ID<undefined>already<undefined>exists<undefined>in<undefined>database<undefined>-<undefined>Choose<undefined>another<undefined>Instr_ID<undefined>to<undefined>insert.';
			END
			ELSE
			BEGIN
				VARIABLES.KOUNT = (SELECT COUNT(*) FROM Products.PRODUCTDATA WHERE Products.PRODUCTDATA.NAME = "NEW".NAME);
				IF(VARIABLES.KOUNT > 0)
				BEGIN
					RAISE SQLEXCEPTION 'Specified<undefined>Name<undefined>already<undefined>exists<undefined>in<undefined>database<undefined>-<undefined>Choose<undefined>another<undefined>Name<undefined>to<undefined>insert.';
				END
				ELSE
				BEGIN
					INSERT INTO Products.PRODUCTDATA (Products.PRODUCTDATA.INSTR_ID, Products.PRODUCTDATA.NAME, Products.PRODUCTDATA.TYPE, Products.PRODUCTDATA.ISSUER, Products.PRODUCTDATA.EXCHANGE, Products.PRODUCTDATA.ISDJI, Products.PRODUCTDATA.ISSP500, Products.PRODUCTDATA.ISNAS100, Products.PRODUCTDATA.ISAMEXINT, Products.PRODUCTDATA.PRIBUSINESS) VALUES ("NEW".INSTR_ID, "NEW".NAME, "NEW".TYPE, "NEW".ISSUER, "NEW".EXCHANGE, "NEW".ISDJI, "NEW".ISSP500, "NEW".ISNAS100, "NEW".ISAMEXINT, "NEW".PRIBUSINESS);
					INSERT INTO Products.PRODUCTSYMBOLS (Products.PRODUCTSYMBOLS.INSTR_ID, Products.PRODUCTSYMBOLS.SYMBOL_TYPE, Products.PRODUCTSYMBOLS.SYMBOL, Products.PRODUCTSYMBOLS.CUSIP) VALUES ("NEW".INSTR_ID, "NEW".SYMBOL_TYPE, "NEW".SYMBOL, "NEW".CUSIP);
				END
			END
		END
	END
END
