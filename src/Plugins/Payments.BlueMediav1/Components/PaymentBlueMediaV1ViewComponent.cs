using Grand.Web.Common.Components;
using Microsoft.AspNetCore.Mvc;

namespace Payments.BlueMediaV1.Controllers
{
    [ViewComponent(Name = "PaymentBlueMediaV1")]
    public class PaymentBlueMediaV1ViewComponent : BaseViewComponent
    {
        public IViewComponentResult Invoke()
        {
            return View(this.GetViewPath());
        }
    }
}