package com.example.usercontact.listener

interface OnCallListener<T> {

    fun onMessage(t: T,states:String)
}