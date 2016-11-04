/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.fb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Assegura acesso ao conteúdo de arquivos "grandes", um ou
 * mais gigabytes, por meio de um offset de tamanho fixo
 * que mantém um "pedaço" do arquivo. Quando requisição é
 * feita para acesso a uma posição que não se encontra no
 * offset, então o "pedação" correspondente é carregado,
 * substituindo o conteúdo anterior.
 */
public class FileBuffer {

    /**
     * Assegura acesso randômico ao arquivo.
     * Utilizado para transferir dados de
     * segmentos para o buffer.
     */
    private RandomAccessFile raf;

    /**
     * Garante acesso rápido ao tamanho do buffer.
     */
    private int bufferSize;

    /**
     * Buffer propriamente dito para o qual
     * segmentos do arquivo serão transferidos.
     */
    private byte[] buffer;

    /**
     * Indica segmento corrente do arquivo que está
     * disponível no buffer.
     */
    private int segmentoCorrente;

    /**
     * ByteBuffer que "embrulha" o buffer, que é
     * constantemente atualizado sob demanda.
     */
    private ByteBuffer bb;

    /**
     * Tamanho do arquivo cujo acesso de leitura
     * é requisitado.
     */
    private long tamanhoArquivo;

    /**
     * Cria buffer para acesso ao arquivo.
     *
     * @param file       O arquivo a ser aberto para leitura.
     * @param bufferSizeInBytes O tamanho do buffer em bytes.
     */
    public FileBuffer(File file, int bufferSizeInBytes) {

        bufferSize = bufferSizeInBytes;

        try {
            tamanhoArquivo = file.length();
            raf = new RandomAccessFile(file, "r");
            buffer = new byte[this.bufferSize];
            bb = ByteBuffer.wrap(buffer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileBuffer construtor");
        }

        // Primeiro uso força primeira carga do buffer.
        segmentoCorrente = -1;
    }

    /**
     * Recupera buffer (como {@link ByteBuffer}).
     *
     * @return O {@link ByteBuffer} que está baseado
     * no buffer corrente.
     */
    public ByteBuffer getByteBuffer() {
        return bb;
    }

    /**
     * Carrega o segmento indicado.
     *
     * @param segmento Segmento do arquivo a ser carregado no offset.
     * @throws RuntimeException Se a operação não pode ser realizada de
     * forma satisfatória.
     */
    public void carregaSegmento(int segmento) {
        try {
            // Posição inicial de leitura
            int inicio = segmento * bufferSize;

            // Bytes disponíveis no arquivo a partir da posição inicial
            long restantes = tamanhoArquivo - inicio;

            // Última posição possível de ser carregada
            int fim = restantes >= bufferSize ? bufferSize : (int)restantes;

            raf.seek(inicio);
            raf.readFully(buffer, 0, fim);
        } catch (IOException e) {
            throw new RuntimeException("FileBuffer.carregaSegmento()");
        }

        segmentoCorrente = segmento;
    }

    /**
     * Se for o caso, carrega o segmento correspondente à posicação
     * antes de torná-la corrente.
     *
     * @param posicao Posição corrente no buffer.
     *
     * @return Deslocamento relativo ao buffer corrente
     * para a posição indicada.
     */
    public int offset(long posicao) {
        long segmento = posicao / bufferSize;
        if (segmento != segmentoCorrente) {
            carregaSegmento((int)segmento);
        }

        return (int) (posicao % bufferSize);
    }
}