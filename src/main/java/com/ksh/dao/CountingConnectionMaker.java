package com.ksh.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker{
    int counter = 0;
    // 실제 사용할 connectionMaker
    // 사용할 connectionMaker를 감싸서 카운팅 기능을 추가했다.
    private ConnectionMaker realConnectionMaker;

    // DI를 받는다.
    public CountingConnectionMaker(ConnectionMaker realConnectionMaker){
        this.realConnectionMaker = realConnectionMaker;
    }

    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        this.counter++;

        return realConnectionMaker.makeConnection();
    }

    public int getCounter(){
        return this.counter;
    }
}
