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
     * Recupera o byte na posição indicada.
     */
    public byte get(long index) {
        return bb.get(offset(index));
    }

    /**
     * Retrieves a four-byte integer starting at the specified index.
     */
    public int getInt(long index) {
        return bb.getInt(offset(index));
    }

    /**
     * Retrieves an eight-byte integer starting at the specified index.
     */
    public long getLong(long index) {
        return bb.getLong(offset(index));
    }

    /**
     * Retrieves a four-byte integer starting at the specified index.
     */
    public short getShort(long index) {
        return bb.getShort(offset(index));
    }

    /**
     * Retrieves a four-byte floating-point number starting at the specified
     * index.
     */
    public float getFloat(long index) {
        return bb.getFloat(offset(index));
    }

    /**
     * Retrieves an eight-byte floating-point number starting at the specified
     * index.
     */
    public double getDouble(long index) {
        return bb.getDouble(offset(index));
    }

    /**
     * Retrieves a two-byte character starting at the specified  index (note
     * that a Unicode code point may require calling this method twice).
     */
    public char getChar(long index) {
        return bb.getChar(offset(index));
    }

    /**
     * Recupera a quantidade de bytes a partir da posição de início.
     *
     * @param posicao Posição de início dos bytes a serem obtidos.
     * @param quantidade Quantidade de bytes a serem retornados.
     *
     * @return Vetor de bytes.
     */
    public byte[] getBytes(long posicao, int quantidade) {
        byte[] bytes = new byte[quantidade];

        bb.position(offset(posicao));
        bb.get(bytes);

        return bytes;
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