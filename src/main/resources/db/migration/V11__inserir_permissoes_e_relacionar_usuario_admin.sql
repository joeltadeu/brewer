INSERT INTO permissao VALUES(1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao VALUES(2, 'ROLE_CADASTRAR_USUARIO');
INSERT INTO permissao VALUES(3, 'ROLE_CADASTRAR_ESTILO');
INSERT INTO permissao VALUES(4, 'ROLE_CADASTRAR_CERVEJA');

INSERT INTO grupo_permissao(grupo_id, permissao_id) VALUES (1, 1);
INSERT INTO grupo_permissao(grupo_id, permissao_id) VALUES (1, 2);
INSERT INTO grupo_permissao(grupo_id, permissao_id) VALUES (1, 3);
INSERT INTO grupo_permissao(grupo_id, permissao_id) VALUES (1, 4);

INSERT INTO usuario_grupo(usuario_id, grupo_id) VALUES (
	(SELECT id from usuario where email = 'admin@gmail.com'), 1
);
