create table leilao(
    id int primary key auto_increment,
	descricao varchar(255),
    `data` date,
    encerrado boolean
);

create table usuario(
	id int primary key auto_increment,
	nome varchar(255)
);

create table pagamento(
	id int primary key auto_increment,
	valor double,
    `data` date
);

create table lances(
	id int primary key auto_increment,
    valor double,
    usuario_id int,
    leilao_id int
);