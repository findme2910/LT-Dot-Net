using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using dotnet_backend.DTO;

namespace dotnet_backend.Service
{
    public interface IUserService
    {
        void register(RegisterDTO dto);

        string login(LoginDTO dto);
    }
}