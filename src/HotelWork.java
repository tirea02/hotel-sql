package src;

import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import static src.SignUp.makeNewAccount;
import static src.UnitTest.connectionTest;

class HotelWork {
    String headMessage="";
    Boolean isLogin = false;
    public static void main(String[] args) {

        //generate new hotelWork
        HotelWork hotelWork = new HotelWork();
        //check if hotel present and load data
        Hotel hotel = new Hotel();

        hotelWork.run(hotel);

    }

    public void run(Hotel hotel) {
        Scanner sc = new Scanner(System.in);
        // check if room is not loaded


        ArrayList<User> users ;
        int choice = 0;


        // Display login or signup options
        System.out.println("**************호텔 예약 시스템*************");

        boolean running = true;
        while (running) {
            System.out.println("1. 로그인");
            System.out.println("2. 회원가입");
            System.out.println("3. 예약상황 확인");
            System.out.println("9. 종료");
            System.out.print("메뉴 선택: ");

            boolean isInputChoiceValid = false;
            while(!isInputChoiceValid) {
                try {
                    choice = sc.nextInt();
                    isInputChoiceValid = true;
                } catch (InputMismatchException e) {
                    System.out.println("메뉴중에 골라주세요 : ");
                    sc.nextLine();
                }
            }
            sc.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> {
                    User loginUser = Login.loginUser(hotel.getConnection());
                    performActions(hotel, loginUser);
                }
                case 2 -> {
                    User newUser = SignUp.makeNewAccount(hotel.getConnection());
                    performActions(hotel, newUser);
                }
                case 3->{
                    hotel.printAllRooms();
                }
                case 9 -> {
                    System.out.println("프로그램을 종료합니다.");
                    running = false;
                }
                default -> System.out.println("잘못된 선택입니다. 다시 선택하세요.");
            }
        }
    }

    public void performActions(Hotel hotel, User user) {
        Scanner scanner = new Scanner(System.in);
        isLogin = true;
        // Perform actions for the logged-in user
        System.out.println("사용자 " + user.getName() + "로 로그인되었습니다.");
        headMessage = user.getName() + "님 환영합니다";

        while (isLogin) {
            System.out.println(headMessage);
            System.out.println("**************메뉴 선택*************");
            System.out.println("1. 로그아웃");
            System.out.println("2. 예약하기");
            System.out.println("3. 예약 취소하기");
            System.out.println("4. 예약 내역 보기");
            System.out.println("5. 개인 정보 수정");
            System.out.println("6. 비밀번호 변경");
            System.out.println("7. 회원 탈퇴");


            System.out.print("메뉴 선택: ");
            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> {
                    System.out.println("로그아웃 되었습니다.");
                    isLogin = false;
                }
                case 2 -> {
                    //예약하기 로직
                    System.out.println("make reservation showing rooms");
                    hotel.printAllRooms();

                    System.out.print("roomNumber : ");
                    String roomNumber = scanner.nextLine();

                    hotel.createReservation(user.getUserId(), roomNumber);

                }
                case 3 -> {
                    // 예약 취소하기 로직
                    System.out.println("cancel reservation showing rooms");
                    hotel.printUserReservedRooms(user);

                    System.out.print("roomNumber : ");
                    String roomNumber = scanner.nextLine();

                    hotel.cancelReservation(user.getUserId(), roomNumber);

                }
                case 4 -> {
                    //개인 예약 내역 보기
                    hotel.printUserReservedRooms(user);
                }
                case 5-> {
                    // 개인 정보 수정 로직

                }
                case 6 -> {
                    // 비밀번호 변경 로직
                }
                case 7 -> {
                    // 회원 탈퇴 로직

                }
                default -> {
                    System.out.println("잘못된 선택입니다. 다시 선택하세요.");
                }
            }
        }

    }// function performAction end
}//HotelWork class END