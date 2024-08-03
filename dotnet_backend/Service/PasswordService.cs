using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.Service
{
    public class PasswordService
    {
        public string HashPassword(string password)
        {
            // Mã hóa mật khẩu
            return BCrypt.Net.BCrypt.HashPassword(password);
        }
        public bool VerifyPassword(string password, string hashedPassword)
        {
            // Xác thực mật khẩu
            return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
        }
    }
}