using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.AccessControl;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using dotnet_backend.CustomException;
using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.Service;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using WebNet.Data;

namespace dotnet_backend.Contronller
{
    [Route("auth")]
    [ApiController]
    public class AuthenticationController : ControllerBase
    {
        IConfiguration configuration;
        JwtHelper jwtHelper;

        ApplicationDBContext applicationDBContext;

        private readonly IUserService _userService;

        public AuthenticationController(IConfiguration configuration, JwtHelper jwtHelper, IUserService userService, ApplicationDBContext applicationDBContext)
        {
            this.applicationDBContext = applicationDBContext;
            this.configuration = configuration;
            this.jwtHelper = jwtHelper;
            _userService = userService;
        }
        [HttpPost("register")]
        [AllowAnonymous]
        public IActionResult Register([FromBody] RegisterDTO dto)
        {
            try
            {
                _userService.register(dto);

                return Ok(new ResponseDTO {response = "Success"});
            }
            catch (UserAlreadyExistsException ex)
            {
                return Conflict(new ResponseDTO{  response = "Conflict" });
            }
            catch (Exception ex)
            {
                // Log the exception (optional)
                return StatusCode(500, new ResponseDTO { response = "Error"});
            }

        }

        [HttpPost("login")]
        [AllowAnonymous]
        public IActionResult Login([FromBody] LoginDTO dto)
        {
            try
            {
                return Ok(new ResponseDTO(_userService.login(dto)));
            }
            catch (UserAlreadyExistsException ex)
            {
                return Conflict(new { Message = ex.Message });
            }
            catch (Exception ex)
            {
                // Log the exception (optional)
                return StatusCode(500, new { Message = ex.Message });
            }
        }
        [HttpGet]
        [Authorize]
        public async Task<IActionResult> checkAsync()
        {
            var userId = HttpContext.Items["UserId"] as string;
            var userEmail = HttpContext.Items["UserEmail"] as string;
            try
            {
                // Thực hiện một truy vấn đơn giản để kiểm tra kết nối
                await applicationDBContext.Database.CanConnectAsync();
                Console.WriteLine(await applicationDBContext.Users.AnyAsync(u => u.Phone == "881273123"));
            }
            catch (Exception ex)
            {
                // Xử lý lỗi kết nối
                Console.WriteLine($"Database connection error: {ex.Message}");
                Console.WriteLine(false);
            }
            if (userId != null)
            {
                return Ok(new { UserId = userId, UserEmail = userEmail });
            }

            return Unauthorized();
        }
        //like
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
        return int.Parse(userId);}
        

        //trả về thông tin của user
         [HttpGet("info")]
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
