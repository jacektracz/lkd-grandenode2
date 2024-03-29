﻿using Grand.Infrastructure.ModelBinding;
using Grand.Infrastructure.Models;
using System;

namespace Grand.Web.Admin.Models.Logging
{
    public partial class LogModel : BaseEntityModel
    {
        [GrandResourceDisplayName("Admin.System.Log.Fields.LogLevel")]
        public string LogLevel { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.ShortMessage")]
        
        public string ShortMessage { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.FullMessage")]
        
        public string FullMessage { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.IPAddress")]
        
        public string IpAddress { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.Customer")]
        public string CustomerId { get; set; }
        [GrandResourceDisplayName("Admin.System.Log.Fields.Customer")]
        public string CustomerEmail { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.PageURL")]
        
        public string PageUrl { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.ReferrerURL")]
        
        public string ReferrerUrl { get; set; }

        [GrandResourceDisplayName("Admin.System.Log.Fields.CreatedOn")]
        public DateTime CreatedOn { get; set; }
    }
}