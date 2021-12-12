using Grand.Infrastructure.ModelBinding;
using Grand.Infrastructure.Models;

namespace Payments.Przelewy24V1.Models
{
    public class ConfigurationModel : BaseModel
    {
        public string StoreScope { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.UseSandbox")]
        public bool UseSandbox { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.BusinessEmail")]
        public string BusinessEmail { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.PDTToken")]
        public string PdtToken { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.PDTValidateOrderTotal")]
        public bool PdtValidateOrderTotal { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.AdditionalFee")]
        public double AdditionalFee { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.AdditionalFeePercentage")]
        public bool AdditionalFeePercentage { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.PassProductNamesAndTotals")]
        public bool PassProductNamesAndTotals { get; set; }

        [GrandResourceDisplayName("Plugins.Payments.Przelewy24V1.Fields.DisplayOrder")]
        public int DisplayOrder { get; set; }



    }
}