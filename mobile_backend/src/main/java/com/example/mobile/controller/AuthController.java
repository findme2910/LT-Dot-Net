package com.example.mobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobile.dto.LoginDTO;
import com.example.mobile.dto.RegisterDTO;
import com.example.mobile.dto.ResponseDTO;
import com.example.mobile.service.UserService;

@RestController
//đánh dấu lớp này như restController, có nghĩa nó có thể xử lý các yêu cầu http và trả về dữ liệu json hoặc xml
@RequestMapping("/auth")
// endpoints trong lớp này là auth
public class AuthController {

	@Autowired
	UserService userService;

	//ánh xạ yêu cầu http post đến /auth/login
	@PostMapping("/login")
	public ResponseEntity<ResponseDTO> login(@RequestBody LoginDTO loginDTO) {
		try {
			//cho phép chuyển đổi nội dung của yêu cầu từ http thành đối tượng login dto
			
			return ResponseEntity.ok(new ResponseDTO(userService.login(loginDTO)));
		} catch (Exception e) {
			//badrequest là trả về 400 và một respon chứa thông điệp ỗi
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage()));
		}

	}


	@PostMapping("/register")
	public ResponseEntity<?> add(@RequestBody RegisterDTO dto) {
		try {
			userService.register(dto);
			return ResponseEntity.ok(new ResponseDTO("Success"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return ResponseEntity.badRequest().body(new ResponseDTO(e.getMessage()));
		}
	}
//	@PostMapping("/")
//	public ResponseEntity<?> add(@RequestBody RegisterDTO dto) {
//		try {
//			userService.register(dto);
//			return ResponseEntity.ok(new ResponseDTO("Success"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return ResponseEntity.badRequest().body(new ResponseDTO("Failure"));
//		}
//	}
}
