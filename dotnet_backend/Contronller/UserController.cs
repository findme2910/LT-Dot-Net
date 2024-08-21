using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;
using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.Service;
using Microsoft.AspNetCore.Mvc;
using WebNet.Data;

namespace dotnet_backend.Contronller
{
    [Route("user")]
    [ApiController]
    public class UserController(IConfiguration configuration, JwtHelper jwtHelper, IUserService userService, ApplicationDBContext applicationDBContext) : ControllerBase
    {
        IConfiguration configuration = configuration;
        JwtHelper jwtHelper = jwtHelper;

        ApplicationDBContext applicationDBContext = applicationDBContext;

        private readonly IUserService _userService = userService;//like
        [HttpPost("like")]
        public IActionResult Like([FromBody] LikeDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.Like(dto, userId);
                return Ok(new { Message = "Success" });
            }
            catch (Exception e)
            {
                return BadRequest(new { Message = e.Message });
            }
        }
        private int GetCurrentUserId()
        {
            var userId = User.FindFirst(ClaimTypes.NameIdentifier)?.Value;
            if (userId == null)
            {
                throw new Exception("User ID not found");
            }
            return int.Parse(userId);
        }

        [HttpGet]
        //trả về thông tin của user
        public IActionResult GetUserInfo()
        {
            try
            {
                var userId = GetCurrentUserId();
                return Ok(_userService.GetUserInfo(userId));
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = ex.Message });
            }
        }

        //trả về số bạn và số lương thông báo của user
        [HttpGet("inf")]
        public IActionResult GetHomeInfo()
        {
            try
            {
                var userId = GetCurrentUserId();
                return Ok(_userService.GetHomeView(userId));
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = ex.Message });
            }
        }

        [HttpPost("comment")]
        public IActionResult AddComment([FromBody] CommentDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.Comment(dto, userId);
                return Ok(new { Message = "Success" });
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = ex.Message });
            }
        }

        [HttpPost("update")]
        public IActionResult UpdateUser([FromBody] UserUpdateDTO dto)
        {
            try
            {
                var userId = GetCurrentUserId();
                _userService.UpdateUser(dto, userId);
                return Ok(new { Message = "Success" });
            }
            catch (Exception ex)
            {
                return BadRequest(new { Message = ex.Message });
            }
        }
    }
}