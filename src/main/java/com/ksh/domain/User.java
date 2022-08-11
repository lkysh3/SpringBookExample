package com.ksh.domain;

public class User {
    String id;
    String name;
    String password;
    Grade grade;
    int login;
    int recommend;
    String email;

    public User(String id, String name, String password, Grade level, int login, int recommend, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.grade = level;
        this.login = login;
        this.recommend = recommend;
        this.email = email;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGrade(Grade level) {
        this.grade = level;
    }

    public Grade getGrade(){
        return grade;
    }

    public void setLogin(int login){
        this.login = login;
    }

    public int getLogin(){
        return login;
    }

    public void setRecommend(int recommend){
        this.recommend = recommend;
    }

    public int getRecommend(){
        return recommend;
    }

    public void setEmail(String email) { this.email = email; }

    public String getEmail() { return email; }

    public void upgradeGrade(){
        Grade nextGrade = this.grade.nextGrade();
        if(nextGrade == null){
            throw new IllegalArgumentException(this.grade + "은 업그레이드가 불가능합니다.");
        }else{
            this.grade = nextGrade;
        }
    }
}
