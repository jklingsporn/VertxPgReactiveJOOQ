package com.ns.vertx.pg;

public class DBQueries {
	
	// create tables::START
	public static String CREATE_AUTHOR_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Author(author_id BIGSERIAL PRIMARY KEY, first_name VARCHAR(30), last_name VARCHAR(30) );";
	public static String CREATE_CATEGORY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Category (category_id BIGSERIAL PRIMARY KEY, name VARCHAR(30), is_deleted boolean);";	
	public static String CREATE_BOOK_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Book( book_id BIGSERIAL PRIMARY KEY, title VARCHAR(255), price double precision, amount INTEGER, is_deleted boolean);";			
	public static String CREATE_ROLE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Role(role_id BIGSERIAL PRIMARY KEY, name VARCHAR(30));";
	public static String CREATE_USER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Users (user_id BIGSERIAL PRIMARY KEY, first_name VARCHAR(30), last_name VARCHAR(30), " +
            "email VARCHAR(30), username VARCHAR(15), password VARCHAR(255), role_id INTEGER REFERENCES Role(role_id) );";
	public static String CREATE_ORDER_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Orders (order_id BIGSERIAL PRIMARY KEY, total double precision, order_date DATE, user_id INTEGER REFERENCES Users(user_id) );";
	public static String CREATE_ORDER_ITEM_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Order_Item ( order_item_id BIGSERIAL PRIMARY KEY, amount INTEGER, book_id BIGINT REFERENCES Book(book_id), order_id BIGINT REFERENCES Orders(order_id) );";
	
	// intermediate tables
	public static String CREATE_AUTHOR_BOOK_TABLE_SQL =  "CREATE TABLE IF NOT EXISTS Author_Book (author_id BIGINT REFERENCES Author(author_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            " book_id BIGINT REFERENCES Book(book_id) ON UPDATE CASCADE ON DELETE CASCADE," +
            " CONSTRAINT Author_Book_pkey PRIMARY KEY (author_id, book_id) );";
	
	public static String CREATE_BOOK_CATEGORY_TABLE_SQL = "CREATE TABLE IF NOT EXISTS Category_Book(category_id BIGINT REFERENCES Category(category_id) ON UPDATE CASCADE ON DELETE CASCADE," +
														    " book_id BIGINT REFERENCES Book(book_id) ON UPDATE CASCADE ON DELETE CASCADE, CONSTRAINT Category_Book_pkey PRIMARY KEY (category_id, book_id) );";	
	// create tables::END	
	
	// Category CRUD queries
	public static String GET_ALL_CATEGORIES_SQL = "SELECT category_id, name, is_deleted FROM Category ORDER BY category_id ASC;";
	public static String GET_CATEGORY_BY_ID_SQL = "SELECT category_id, name, is_deleted FROM Category WHERE category_id = $1;";
	public static String CREATE_CATEGORY_SQL = "INSERT INTO Category (name, is_deleted) VALUES($1, $2) RETURNING category_id;";
	public static String UPDATE_CATEGORY_SQL = "UPDATE Category SET name = $1, is_deleted = $2 WHERE category_id = $3 RETURNING category_id;"; 
	public static String DELETE_CATEGORY_BY_ID_SQL = "DELETE FROM Category WHERE category_id = $1;";
	
	// Book SELECT queries
	// FIXME: refactor ARRAY_AGG function to take multiple fileds from 'author'/'category' table
	public static String GET_ALL_BOOKS = "SELECT b.book_id AS b_id, b.title, b.price, b.amount, b.is_deleted, "
			+ "ARRAY_AGG(aut.author_id) as aut_ids, ARRAY_AGG(cat.category_id) as cat_ids FROM book b " +
            "LEFT JOIN author_book ON author_book.book_id = b.book_id " +
            "LEFT JOIN author aut ON aut.author_id = author_book.author_id " +
            "LEFT JOIN category_book ON category_book.book_id = b.book_id " +
            "LEFT JOIN category cat ON cat.category_id = category_book.category_id " + 
            "GROUP BY b_id ORDER BY b_id ASC;";
	
	
	public static String GET_BOOK_BY_BOOK_ID = "SELECT b.book_id AS b_id, b.title, b.price, b.amount, b.is_deleted, " +
			"ARRAY_AGG(aut.author_id) as aut_ids, " +
            "ARRAY_AGG(cat.category_id) as cat_ids FROM book b " +
            "LEFT JOIN author_book ON author_book.book_id = b.book_id " +
            "LEFT JOIN author aut ON aut.author_id = author_book.author_id " +
            "LEFT JOIN category_book ON category_book.book_id = b.book_id " +
            "LEFT JOIN category cat ON cat.category_id = category_book.category_id " +
            "WHERE b.book_id = :id " + 
            "GROUP BY b_id ORDER BY b_id ASC;";
	
	public static String GET_BOOK_BY_BOOK_ID_VER2 = "SELECT b.book_id AS b_id, b.title, b.price, b.amount, b.is_deleted, " +
			"ARRAY_AGG('{' || aut.author_id || ',' || aut.first_name || ',' || aut.last_name ||  '}' ) as aut_ids, " +  
			"ARRAY_AGG('{' || cat.category_id || ',' || cat.name || ',' || cat.is_deleted || '}' ) as cat_ids FROM book b " + 
            "LEFT JOIN author_book ON author_book.book_id = b.book_id " +
            "LEFT JOIN author aut ON aut.author_id = author_book.author_id " +
            "LEFT JOIN category_book ON category_book.book_id = b.book_id " +
            "LEFT JOIN category cat ON cat.category_id = category_book.category_id " +
            "WHERE b.book_id = :id " + 
            "GROUP BY b_id ORDER BY b_id ASC;";
	
	public static String GET_BOOK_BY_BOOK_ID_VER3 = "SELECT b.book_id AS b_id, b.title, b.price, b.amount, b.is_deleted, " +
			"ARRAY_AGG( aut.author_id || ',' || aut.first_name || ',' || aut.last_name) as aut_ids, " +  
			"ARRAY_AGG( cat.category_id || ',' || cat.name || ',' || cat.is_deleted) as cat_ids FROM book b " + 
            "LEFT JOIN author_book ON author_book.book_id = b.book_id " +
            "LEFT JOIN author aut ON aut.author_id = author_book.author_id " +
            "LEFT JOIN category_book ON category_book.book_id = b.book_id " +
            "LEFT JOIN category cat ON cat.category_id = category_book.category_id " +
            "WHERE b.book_id = :id " + 
            "GROUP BY b_id ORDER BY b_id ASC;";
	
	
	public static String GET_BOOK_BY_BOOK_ID_VER4 = "SELECT b.book_id AS b_id, b.title, b.price, b.amount, b.is_deleted, " +
			"JSON_AGG(JSON_BUILD_OBJECT( 'authorId', aut.author_id, 'firstName', aut.first_name, 'lastName' , aut.last_name )) as aut_ids, " +  
			"JSON_AGG(JSON_BUILD_OBJECT( 'categoryId', cat.category_id, 'name', cat.name, 'isDeleted' , cat.is_deleted )) as cat_ids " + 
			"FROM book b " + 
            "LEFT JOIN author_book ON author_book.book_id = b.book_id " +
            "LEFT JOIN author aut ON aut.author_id = author_book.author_id " +
            "LEFT JOIN category_book ON category_book.book_id = b.book_id " +
            "LEFT JOIN category cat ON cat.category_id = category_book.category_id " +
            "WHERE b.book_id = :id " + 
            "GROUP BY b_id ORDER BY b_id ASC;";
	
}
