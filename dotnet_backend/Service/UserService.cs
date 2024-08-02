using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using dotnet_backend.CustomException;
using dotnet_backend.DTO;
using dotnet_backend.Helper;
using dotnet_backend.Model;
using Microsoft.EntityFrameworkCore;
using WebNet.Data;

namespace dotnet_backend.Service
{
    public class UserService : IUserService
    {
        private readonly PasswordService _passwordHasher; // Interface cho mã hóa mật khẩu
        private readonly IConfiguration _configuration;
        private readonly ApplicationDBContext _context;

        private readonly JwtHelper jwtHelper;
        public UserService(PasswordService passwordService, JwtHelper jwtHelper , IConfiguration configuration, ApplicationDBContext applicationDBContext)
        {
            _passwordHasher = passwordService;
            _configuration = configuration;
            _context = applicationDBContext;
            this.jwtHelper = jwtHelper;
        }
        public void register(RegisterDTO dto)
        {
            if (_context.Users.Any(u => u.Phone == dto.Phone))
            {
                throw new UserAlreadyExistsException("Phone number already exists.");
            }
            if (!Enum.TryParse<Gender>(dto.Gender, out var gender))
            {
                throw new InvalidGenderException("Invalid gender provided.");
            }
            var user = new User
            {
                Phone = dto.Phone,
                Name = dto.Name,
                Password = _passwordHasher.HashPassword(dto.Password),
                Comments = new List<Comment>(),
                Posts = new List<Post>(),
                Gender = gender,
                Birth = new DateTime(dto.Birth), // Chuyển đổi từ long hoặc Unix timestamp nếu cần
                Avatar = Convert.FromBase64String(_configuration["res:avatar:male"]) // Thay đổi tùy thuộc vào cách xử lý blob
            };

            _context.Users.Add(user);
            _context.SaveChanges();
        }
        public string login(LoginDTO loginDTO)
        {
            var user =  _context.Users.First(u => u.Phone == loginDTO.Phone);
            if (user == null)
            {
                throw new Exception("Phone number does not exist");
            }

            if (_passwordHasher.VerifyPassword(loginDTO.Password,user.Password))
            {
                return jwtHelper.GenerateJwtToken(user.Id);
            }
            else
            {
                throw new Exception("Password incorrect");
            }
        }
    }
}