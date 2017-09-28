// IBookManager.aidl
package com.demo.nick.ipcstudy;

// Declare any non-default types here with import statements
import com.demo.nick.ipcstudy.Book;
import com.demo.nick.ipcstudy.IOnNewBookArrivedListener;
interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     List<Book> getBookList();
     void addBook(in Book book);
     void registerListener(IOnNewBookArrivedListener listener);
     void unregisterListener(IOnNewBookArrivedListener listener);
}
