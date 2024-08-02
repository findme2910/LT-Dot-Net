using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using Microsoft.IdentityModel.Tokens;

namespace dotnet_backend.Helper
{
    public class JwtHelper
    {
        private readonly string _secretKey;
        private readonly string _issuer;
        private readonly string _audience;

        public JwtHelper(string secretKey, string issuer, string audience)
        {
            _secretKey = secretKey;
            _issuer = issuer;
            _audience = audience;
        }

        public string GenerateJwtToken(int UserId)
        {
            var signinCredentials = new SigningCredentials(new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_secretKey)), SecurityAlgorithms.HmacSha256);
            var subject = new ClaimsIdentity(new[] { new Claim(JwtRegisteredClaimNames.Sub, UserId+""), new Claim(JwtRegisteredClaimNames.Email, UserId+"") });
            var expried = DateTime.UtcNow.AddMinutes(60);
            var tokenDesciptions = new SecurityTokenDescriptor
            {
                Subject = subject,
                Expires = expried,
                Issuer = _issuer,
                Audience = _audience,
                SigningCredentials = signinCredentials
            };
            var tokenHandler = new JwtSecurityTokenHandler();
            var token = tokenHandler.CreateToken(tokenDesciptions);
            var jwtToken = tokenHandler.WriteToken(token);
            return jwtToken;
        }

        public string GetSubjectFromToken(string token)
        {
            var tokenHandler = new JwtSecurityTokenHandler();

            try
            {
                // Validate token
                var validationParameters = new TokenValidationParameters
                {
                    ValidIssuer = _issuer,
                    ValidAudience = _audience,
                    IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_secretKey)),
                    ValidateIssuer = true,
                    ValidateAudience = false,
                    ValidateLifetime = false,
                    ValidateIssuerSigningKey = true
                };

                SecurityToken validatedToken;
                ClaimsPrincipal principal = tokenHandler.ValidateToken(token, validationParameters, out validatedToken);

                // Get subject (usually username or user id)
                var subjectClaim = principal.FindFirst(JwtRegisteredClaimNames.Sub);

                return subjectClaim?.Value;
            }
            catch (Exception ex)
            {
                // Handle token validation exceptions
                Console.WriteLine($"Token validation failed: {ex.Message}");
                return null;
            }
        }
    }
}