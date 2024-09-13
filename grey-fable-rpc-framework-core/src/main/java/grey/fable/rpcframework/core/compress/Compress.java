package grey.fable.rpcframework.core.compress;

import grey.fable.rpcframework.core.extension.SPI;

/**
 * 压缩接口.
 *
 * @author GreyFable
 * @since 2024/8/28 17:06
 */
@SPI
public interface Compress {

    byte[] compress(byte[] bytes);


    byte[] decompress(byte[] bytes);
}
