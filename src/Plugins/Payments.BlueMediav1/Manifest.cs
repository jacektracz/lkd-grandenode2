using Grand.Infrastructure;
using Grand.Infrastructure.Plugins;
using Payments.BlueMediaV1;

[assembly: PluginInfo(
    FriendlyName = "Blue Media Standard",
    Group = "Payment methods",
    SystemName = BlueMediaV1PaymentDefaults.ProviderSystemName,
    SupportedVersion = GrandVersion.SupportedPluginVersion,
    Author = "lakida team",
    Version = "1.00"
)]