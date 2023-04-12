using Org.BouncyCastle.Crypto.Signers;
using Org.BouncyCastle.Math;

namespace iText7.Net_Playground.Signature
{
    internal class EcdsaFormatHelper
    {
        static internal byte[] PlainToDer(byte[] plain)
        {
            int valueLength = plain.Length / 2;
            BigInteger r = new BigInteger(1, plain, 0, valueLength);
            BigInteger s = new BigInteger(1, plain, valueLength, valueLength);

            return StandardDsaEncoding.Instance.Encode(null, r, s);
        }
    }
}
