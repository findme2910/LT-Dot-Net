using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.DTO
{
    public class ResponseDTO
    {
        public string response { get; set; }

        // Constructor with parameter
        public ResponseDTO(string response)
        {
            this.response = response;
        }

        // Parameterless constructor
        public ResponseDTO() { }
    }
}