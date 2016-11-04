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

    private File _file;
    private RandomAccessFile raf;
    private int _bufferSize;              // long because it's used in long expressions
    private byte[] _buffer;
    private int segmentoCorrente;
    private ByteBuffer bb;
    private long tamanhoArquivo;

    /**
     * Cria offset para acesso ao arquivo.
     *
     * @param file       O arquivo a ser aberto para leitura.
     * @param bufferSize O tamanho do offset.
     */
    public FileBuffer(File file, int bufferSize) {

        _file = file;
        _bufferSize = bufferSize;

        try {
            tamanhoArquivo = _file.length();
            raf = new RandomAccessFile(file, "r");
            _buffer = new byte[_bufferSize];
            bb = ByteBuffer.wrap(_buffer);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileBuffer construtor");
        }

        // Primeiro uso força primeira carga do buffer.
        segmentoCorrente = -1;
    }

    public ByteBuffer getByteBuffer() {
        return bb;
    }

    /**
     * Carrega o segmento indicado.
     *
     * @param segmento Segmento do arquivo a ser carregado no offset.
     * @throws IOException Se a operação não pode ser realizada de
     * forma satisfatória.
     */
    public void carregaSegmento(int segmento) {
        try {
            // Posição inicial de leitura
            int inicio = segmento * _bufferSize;

            // Bytes disponíveis no arquivo a partir da posição inicial
            long restantes = tamanhoArquivo - inicio;

            // Última posição possível de ser carregada
            int fim = restantes >= _bufferSize ? _bufferSize : (int)restantes;

            raf.seek(inicio);
            raf.readFully(_buffer, 0, fim);
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
        long segmento = posicao / _bufferSize;
        if (segmento != segmentoCorrente) {
            carregaSegmento((int)segmento);
        }

        return (int) (posicao % _bufferSize);
    }
}