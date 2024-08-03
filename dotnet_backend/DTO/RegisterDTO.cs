using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.DTO
{
    public class RegisterDTO
    {
        public string Name { get; set; }
        public string Phone { get; set; }
        public string Gender { get; set; }
        public long Birth { get; set; }
        public string Password { get; set; }
    }
    public class Builder
    {
        private readonly RegisterDTO _dto = new RegisterDTO();

        public Builder WithName(string name)
        {
            _dto.Name = name;
            return this;
        }

        public Builder WithPhone(string phone)
        {
            _dto.Phone = phone;
            return this;
        }

        public Builder WithGender(string gender)
        {
            _dto.Gender = gender;
            return this;
        }

        public Builder WithBirth(long birth)
        {
            _dto.Birth = birth;
            return this;
        }

        public Builder WithPassword(string password)
        {
            _dto.Password = password;
            return this;
        }

        public RegisterDTO Build()
        {
            return _dto;
        }
    }
}