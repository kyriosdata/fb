# fb (file buffer)
Acesso ao conteúdo de grandes arquivos por meio de buffers. 

[<img src="https://api.travis-ci.org/kyriosdata/fb.svg?branch=master">](https://travis-ci.org/kyriosdata/fb)
[![Dependency Status](https://www.versioneye.com/user/projects/581bd12dafb6141c1c4bf023/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/581bd12dafb6141c1c4bf023)
[![Sonarqube](https://sonarqube.com/api/badges/gate?key=com.github.kyriosdata.fb:fb)](https://sonarqube.com/dashboard/index?id=com.github.kyriosdata.fb%3Afb)
[![Javadocs](http://javadoc.io/badge/com.github.kyriosdata.fb/fb.svg)](http://javadoc.io/doc/com.github.kyriosdata.fb/fb)

<br />
<a rel="license" href="http://creativecommons.org/licenses/by/4.0/">
<img alt="Creative Commons License" style="border-width:0"
 src="https://i.creativecommons.org/l/by/4.0/88x31.png" /></a>
 <br />This work is licensed under a <a rel="license" 
 href="http://creativecommons.org/licenses/by/4.0/">Creative Commons 
 Attribution 4.0 International License</a>. 
 <br />Fábio Nogueira de Lucena - Fábrica de Software - 
 Instituto de Informática (UFG).

## Caso de uso
Alguns arquivos podem atingir dezenas, centenas de
megabytes, ou ainda mais. Dependendo do uso que se faz da informação
armazenada em "grandes" arquivos, a estratégia pode ser tão simples
quanto ler trechos sequenciais até que todo o arquivo seja percorrido.
Em outros casos, quando o acesso não possui uma ordem
de acesso que pode ser prevista, ou quando vários 
clientes concorrentes, mesmo que exclusivamente para leitura, requisitam
acesso ao conteúdo desse arquivo, a estratégia "simples" não é mais 
uma solução razoável. 

Esse componente de software tem como propósito gerir o acesso ao
conteúdo de arquivos que podem conter centenas de gigabytes de 
dados, consumidos de forma concorrente por vários clientes.


## Como usar (via maven)?

Acrescente a dependência no arquivo pom.xml:

<pre>
&lt;dependency&gt;
  &lt;groupId&gt;com.github.kyriosdata.fb&lt;/groupId&gt;
  &lt;artifactId&gt;fb&lt;/artifactId&gt;
  &lt;version&gt;1.0.0&lt;/version&gt;
&lt;/dependency&gt;
</pre>
