package top.liujingyanghui.assignmentupload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.liujingyanghui.assignmentupload.service.UserService;

@SpringBootTest
class AssignmentUploadApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
//        for (int i = 10000; i < 10030; i++) {
//            User user = new User();
//            user.setPassword("111111");
//            user.setEmail(i+"@qq.com");
//            user.setNumber(Integer.toString(i));
//            user.setName("2");
//            user.setSchoolId(1680);
//            user.setLastLoginTime(LocalDateTime.now());
//            user.setCreateTime(LocalDateTime.now());
////            user.setRole(user.getRole().equals("1") ? "ROLE_TEACHER" : "ROLE_STUDENT");
//            user.setRole("ROLE_STUDENT");
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//            user.setPassword(encoder.encode(user.getPassword()));
//            user.setStatus(1);
//            String code = UUID.randomUUID().toString();
//            user.setActiveCode(code);
//            userService.save(user);
//        }
    }

//    @Test
//    void test1(){
//        for (int i = 43; i <59 ; i++) {
//            User user = userService.getById(i);
//            user.setClassId(47L);
//            System.out.println(userService.updateById(user));
//        }
//    }
}
