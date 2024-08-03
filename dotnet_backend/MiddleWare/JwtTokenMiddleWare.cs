using System;
using System.Collections.Generic;
using System.IdentityModel.Tokens.Jwt;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.MiddleWare
{
    public class JwtTokenMiddleWare
    {
        private readonly RequestDelegate _next;

        public JwtTokenMiddleWare(RequestDelegate next)
        {
            _next = next;
        }

        public async Task Invoke(HttpContext context)
        {
            var authorizationHeader = context.Request.Headers["Authorization"].FirstOrDefault();
            var token = authorizationHeader?.Substring("Bearer ".Length).Trim();
           
            if (token != null)
            {
                var tokenHandler = new JwtSecurityTokenHandler();
                var jwtToken = tokenHandler.ReadToken(token) as JwtSecurityToken;

                if (jwtToken != null)
                {
                    var userId = jwtToken.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Sub)?.Value;
                    var userEmail = jwtToken.Claims.FirstOrDefault(c => c.Type == JwtRegisteredClaimNames.Email)?.Value;

                    // Bạn có thể lưu thông tin vào HttpContext để sử dụng sau này
                    context.Items["UserId"] = userId;
                    context.Items["UserEmail"] = userEmail;
                }
            }

            await _next(context);
        }
    }
}