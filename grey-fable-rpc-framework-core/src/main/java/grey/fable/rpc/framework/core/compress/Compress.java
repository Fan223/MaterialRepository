package grey.fable.rpc.framework.core.compress;

import grey.fable.rpc.framework.core.extension.SPI;

/**
 * 压缩接口.
 *
 * @author GreyFableaa
 * @since 2024/8/28 17:06
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
