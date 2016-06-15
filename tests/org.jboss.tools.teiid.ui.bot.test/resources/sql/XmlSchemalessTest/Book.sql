SELECT ISBN, TITLE, convert(EDITION, string) AS edition, NAME AS publisherName, LOCATION AS publisherLocation 
FROM Books.BOOKS, Books.PUBLISHERS 
WHERE PUBLISHER = PUBLISHER_ID