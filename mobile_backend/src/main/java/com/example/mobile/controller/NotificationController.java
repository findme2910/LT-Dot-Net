package com.example.mobile.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mobile.dto.RequestNotificationDTO;
import com.example.mobile.mapper.UserMapper;
import com.example.mobile.service.NotificationService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
// đây là api lấy ra thông báo
@RestController
@RequestMapping("/noti")
public class NotificationController {
	@Autowired
	private NotificationService notificationService;

	//Đây là http get ra thông báo
	@GetMapping
	public ResponseEntity<?> getNotifications(@ModelAttribute RequestNotificationDTO dto) {
		// trả về một responEntity.ok là trạng thái 200 và phần thân phản hồi được truyền vào bên trong
		return ResponseEntity
				// gọi thông báo getNotis ừ notificationSevice và chuyển danh sách thông báo thành luồng stream để thực hiện cho việc biến dổi
				.ok(notificationService.getNotis(dto).stream()// stream cung cấp phương thức map,reduce để thao tác với dữ liệu không phải sử dụng các vòng lặp
						.map(UserMapper.INSTANCE::NotificationToDTO).toList());
	}

}
