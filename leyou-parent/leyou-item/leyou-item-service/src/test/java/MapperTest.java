import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.ItemApplication;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.po.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author: HuYi.Zhang
 * @create: 2018-03-30 17:40
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ItemApplication.class)
public class MapperTest {

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void test1() {
        Brand brand = this.brandMapper.selectByPrimaryKey(2L);
        System.out.println(brand.getId());
        System.out.println(brand.getName());
        System.out.println(brand.getLetter());
    }

    @Test
    public void testFastDFS() throws FileNotFoundException {
        File file = new File("D:\\test\\ly.jpg");
        StorePath storePath = this.storageClient.uploadFile(new FileInputStream(file), file.length(), "jpg", null);
        System.out.println("getFullPath: " + storePath.getFullPath());
        System.out.println("getPath: " + storePath.getPath());
        System.out.println("getGroup: " + storePath.getGroup());
        System.out.println(storePath);
    }

    @Test
    public void sendMessage() {
        // 发送消息
        try {
            this.amqpTemplate.convertAndSend("item.update",82l);
        } catch (AmqpException e) {
        }
    }
}
