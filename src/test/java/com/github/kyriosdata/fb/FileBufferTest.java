package com.github.kyriosdata.fb;

import org.junit.jupiter.api.Test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBufferTest {

    public void criaArquivoParaTestes() throws IOException {
        File f = new File("ints.dat");
        FileOutputStream fos = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fos);

        for (int i = 0; i < 256; i++) {
            dos.writeInt(i);
        }

        dos.flush();
        dos.close();
    }

    @Test
    public void percorreTodoOArquivo() {
        File f = new File("src/test/resources/ints.dat");
        FileBuffer fb = new FileBuffer(f, 100);

        ByteBuffer bb = fb.getByteBuffer();

        for(int i = 0; i < 256; i++) {
            int offset = fb.offset(i * 4);
            assertEquals(i, bb.getInt(offset));
        }
    }
}

