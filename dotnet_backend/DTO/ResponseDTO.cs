using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace dotnet_backend.DTO
{
    public class ResponseDTO
    {
        public string Response { get; set; }

        // Constructor with parameter
        public ResponseDTO(string response)
        {
            Response = response;
        }

        // Parameterless constructor
        public ResponseDTO() { }
    }
}