
CREATE FOREIGN TABLE myTable (
	Column1 string(4000)
	CONSTRAINT UniqueConstraint UNIQUE(Column1)
) OPTIONS(NAMEINSOURCE 'myTableSource', UPDATABLE 'TRUE')

