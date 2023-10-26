package Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

class Phone {
    private String name;
    private String phoneNumber;
    private String address;


    Phone(String name, String phoneNumber, String address) {
        setName(name);
        setPhoneNumber(phoneNumber);
        setAddress(address);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

class SQLConnect {

    PreparedStatement preparedStatement(String sql) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/test";
        String password = "1234";
        String id = "root";
        Connection cnn = DriverManager.getConnection(url, id, password);
        return cnn.prepareStatement(sql);
    }

    void sqlExecute(Phone phone, PreparedStatement pst) {
        try {
            pst.setString(1, phone.getName());
            pst.setString(2, phone.getPhoneNumber());
            pst.setString(3, phone.getAddress());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            pst.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    int sqlUpdate(String name, PreparedStatement pst) throws SQLException {
        pst.setString(1, name);
        return pst.executeUpdate();
    }


    ResultSet sqlQuery(PreparedStatement pst) throws SQLException {
        return pst.executeQuery();
    }

    ResultSet sqlQuery(String name, PreparedStatement pst) throws SQLException {
        pst.setString(1, name);
        return pst.executeQuery();
    }


    void print(int a) {
        System.out.println(a + "개 항목 작업 완료했습니다.");
    }

    void getTable(ResultSet rs) {
        String[] columns = {"이름", "전화번호", "주소"};
        try {
            ResultSetMetaData rsd = rs.getMetaData();
            int count = 0;
            while (rs.next()) {
                count++;
                for (int i = 1; i < rsd.getColumnCount() + 1; i++) {
                    System.out.print(columns[i - 1] + " : " + rs.getString(i));
                    if (i != rsd.getColumnCount()) {
                        System.out.print(", ");
                    }
                }
                System.out.println();
            }
            if (count == 0) {
                System.out.println("전화번호부에 없습니다.");
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
    }

    ArrayList<Phone> setJavaTable(ResultSet rs) {
        ArrayList<Phone> phones = new ArrayList<>();
        try {
            while (rs.next()) {
                phones.add(new Phone(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.fillInStackTrace();
        }
        return phones;
    }
}

public class Test20231026 {
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        SQLConnect scn = new SQLConnect();
        ArrayList<Phone> phones;
        int input = 0;
        while (input != 5) {
            System.out.println("1. 입력\t2. 검색\t3. 삭제\t4. 출력\t5. 종료");
            input = sc.nextInt();
            switch (input) {
                case 1:
                    System.out.print("이름 : ");
                    String name = sc.next();
                    System.out.print("\n전화번호 : ");
                    String num = sc.next();
                    System.out.print("\n주소 : ");
                    sc.nextLine();
                    String address = sc.nextLine();
                    Phone temp = new Phone(name, num, address);
                    scn.sqlExecute(temp, scn.preparedStatement("insert into phone values (?,?,?)"));
                    break;
                case 2:
                    System.out.print("이름 : ");
                    scn.getTable(scn.sqlQuery(sc.next(), scn.preparedStatement("select * from phone where name = ?")));
                    break;
                case 3:
                    System.out.print("이름 : ");
                    scn.print(scn.sqlUpdate(sc.next(), scn.preparedStatement("delete from phone where name = ?")));
                    break;
                case 4:
                    scn.getTable(scn.sqlQuery(scn.preparedStatement("select * from phone")));
                    break;
                case 5:
                    break;
                default:
                    System.out.print("잘못된 입력입니다.");
            }
            System.out.println();
            phones = scn.setJavaTable(scn.sqlQuery(scn.preparedStatement("select * from phone"))); // 매번 명령이 끝날 떄 마다 자바의 phone Arraylist - MySQL 테이블 연동
        }
    }
}
