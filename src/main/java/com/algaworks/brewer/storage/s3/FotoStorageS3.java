package com.algaworks.brewer.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

@Profile("production")
@Component
public class FotoStorageS3 implements FotoStorage {

	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);
	private static final String BUCKET = "jbeer";

	@Autowired
	private AmazonS3 amazonS3;

	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());

			AccessControlList acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

			try {
				enviarFoto(novoNome, arquivo, acl);
				enviarThumbnail(novoNome, arquivo, acl);
			} catch (IOException e) {
				throw new RuntimeException("{upload.foto.exception.erro.salvar.foto}", e);
			}
		}
		return novoNome;
	}

	@Override
	public byte[] recuperarFoto(String foto) {
		return recuperar(foto);
	}

	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(FotoStorage.THUMBNAIL_PREFIX + foto);
	}

	@Override
	public void delete(String foto) {
		amazonS3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, FotoStorage.THUMBNAIL_PREFIX + foto));
	}

	@Override
	public String getUrl(String foto) {
		return (!StringUtils.isEmpty(foto)) ? "https://s3-sa-east-1.amazonaws.com/jbeer/" + foto : null;
	}

	private byte[] recuperar(String foto) {
		InputStream is = amazonS3.getObject(BUCKET, foto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("{upload.foto.exception.erro.lendo.foto}");
		}
		return null;
	}

	private void enviarFoto(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());

		amazonS3.putObject(
				new PutObjectRequest(BUCKET, novoNome, arquivo.getInputStream(), metadata).withAccessControlList(acl));
	}

	private void enviarThumbnail(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);

		byte[] array = os.toByteArray();
		InputStream is = new ByteArrayInputStream(array);

		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(array.length);

		amazonS3.putObject(new PutObjectRequest(BUCKET, THUMBNAIL_PREFIX + novoNome, is, thumbMetadata)
				.withAccessControlList(acl));
	}
}
