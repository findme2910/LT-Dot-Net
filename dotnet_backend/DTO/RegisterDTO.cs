using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.DTO
{
    public class RegisterDTO
    {
        public string name { get; set; }
        public string phone { get; set; }
        public string gender { get; set; }
        public long birth { get; set; }
        public string password { get; set; }
    }
    public class Builder
    {
        private readonly RegisterDTO _dto = new RegisterDTO();

        public Builder WithName(string name)
        {
            _dto.name = name;
            return this;
        }

        public Builder WithPhone(string phone)
        {
            _dto.phone = phone;
            return this;
        }

        public Builder WithGender(string gender)
        {
            _dto.gender = gender;
            return this;
        }

        public Builder WithBirth(long birth)
        {
            _dto.birth = birth;
            return this;
        }

        public Builder WithPassword(string password)
        {
            _dto.password = password;
            return this;
        }

        public RegisterDTO Build()
        {
            return _dto;
        }
    }
}