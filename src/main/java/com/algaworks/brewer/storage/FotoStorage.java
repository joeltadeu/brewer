package com.algaworks.brewer.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {

	public static final String THUMBNAIL_PREFIX = "thumbnail."; 
	
	public String salvar(MultipartFile[] files);

	public byte[] recuperarFoto(String nome);

	public byte[] recuperarThumbnail(String foto);

	public void delete(String foto);

	public String getUrl(String nomeFoto);
	
	default String renomearArquivo(String nomeOriginal) {
		return UUID.randomUUID().toString() + "_" + nomeOriginal;
	}
}
