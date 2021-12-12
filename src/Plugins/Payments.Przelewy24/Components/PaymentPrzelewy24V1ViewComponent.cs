using Grand.Web.Common.Components;
using Microsoft.AspNetCore.Mvc;

namespace Payments.Przelewy24V1.Controllers
{
    [ViewComponent(Name = "PaymentPrzelewy24V1")]
    public class PaymentPrzelewy24V1ViewComponent : BaseViewComponent
    {
        public IViewComponentResult Invoke()
        {
            return View(this.GetViewPath());
        }
    }
}