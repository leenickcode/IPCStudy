// IOnNewBookArrivedListener.aidl
package com.demo.nick.ipcstudy;

// Declare any non-default types here with import statements
import com.demo.nick.ipcstudy.Book;
interface IOnNewBookArrivedListener {
  void onNewBookArrived(in Book book);
}
