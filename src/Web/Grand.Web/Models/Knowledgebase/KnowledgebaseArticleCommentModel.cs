﻿using Grand.Infrastructure.Models;
using System;

namespace Grand.Web.Models.Knowledgebase
{
    public partial class KnowledgebaseArticleCommentModel : BaseEntityModel
    {
        public string CustomerId { get; set; }

        public string CustomerName { get; set; }

        public string CommentText { get; set; }

        public DateTime CreatedOn { get; set; }
    }
}
