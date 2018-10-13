Departamento:

	GetAll: localhost:3000/academico/department/
	Post: localhost:3000/academico/department/
	Get unique: localhost:3000/academico/department/{id}
	Put: localhost:3000/academico/department/{id}
	Delete: localhost:3000/academico/department/{id}
	
	Get secretarias: localhost:3000/academico/department/{id}/secretaries
	Post secretarias: localhost:3000/academico/department/{id}/secretaries

Secretaria:

	GetAll: localhost:3000/academico/secretary/
	Post: localhost:3000/academico/secretary/
	Get by id: localhost:3000/academico/secretary/{id}
	Put: localhost:3000/academico/secretary/{id}
	Delete: localhost:3000/academico/secretary/{id}

	Get cursos: localhost:3000/academico/secretary/{id}/courses
	Post cursos: localhost:3000/academico/secretary/{id}/course

Curso:

	GetAll: localhost:3000/academico/course/
	Post: localhost:3000/academico/course/
	Get by id: localhost:3000/academico/course/{id}
	Put: localhost:3000/academico/course/{id}
	Delete: localhost:3000/academico/course/{id}

	Get disciplinas: localhost:3000/academico/course/{id}/disciplines
	Post disciplinas: localhost:3000/academico/course/{id}/discipline

Disciplina:

	GetAll: localhost:3000/academico/discipline/
	Post: localhost:3000/academico/discipline/
	Get by id: localhost:3000/academico/discipline/{id}
	Put: localhost:3000/academico/discipline/{id}
	Delete: localhost:3000/academico/discipline/{id}

Professor:

	GetAll: localhost:3000/academico/professor/
	Post: localhost:3000/academico/professor/
	Get unique: localhost:3000/academico/professor/{id}
	Put: localhost:3000/academico/professor/{id}
	Delete: localhost:3000/academico/professor/{id}
	(Post) Registrar em uma disciplina: localhost:3000/academico/professor/{idP}/discipline/{idD}
		
Aluno:

	GetAll: localhost:3000/academico/student/
	Post: localhost:3000/academico/student/
	Get by id: localhost:3000/academico/student/{id}
	Put: localhost:3000/academico/student/{id}
	Delete: localhost:3000/academico/student/{id}
	(Post) Registrar em ums disciplina:	localhost:3000/academico/student/{idS}/discipline/{idD}
	GetAll verificar as dsciplinas registradas: localhost:3000/academico/student/{idS}/discipline/
	(Post) finalizar o andamento de uma disciplina: localhost:3000/academico/student/{idS}/complete/{idD}
	Get histórico: localhost:3000/academico/matricualate/history/{id}


	Departamento: {"name": ,"secretaries": []}
	Secretaria: {"type": }
	Curso: {"name": }
	Discipline: {"name": ,"code": ,"credits": ,"requiredCredits":  ,"requiredDisciplines": []}
	Aluno: {"firstName": ,"lastName": }
	Professor: {"firstName":"","lastName": }
