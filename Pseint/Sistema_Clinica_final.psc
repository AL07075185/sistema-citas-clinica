// SISTEMA DE ADMINISTRACION DE CITAS - CONSULTORIO CLINICO
// Pseudocodigo PSeInt - Avance 1 (Computacion en Java)
// Equivalencia con el diagrama de clases:
// - Los arreglos simulan los archivos CSV de la carpeta db
// - BuscarIndice  -> Gestor*.buscarPorId() / existeId()
// - MenuDoctores  -> SistemaClinica.menuDoctores()
// - MenuPacientes -> SistemaClinica.menuPacientes()
// - MenuCitas     -> SistemaClinica.menuCitas()
// --------- Funcion: busca un ID y devuelve su posicion (-1 si no existe)
Función indice <- BuscarIndice (arreglo,cantidad,idBuscado)
	Definir indice Como Entero
	Definir i Como Entero
	indice <- -1
	Si cantidad>0 Entonces
		Para i<-1 Hasta cantidad Hacer
			Si arreglo[i]=idBuscado Entonces
				indice <- i
			FinSi
		FinPara
	FinSi
FinFunción

// --------- SubProceso: modulo de doctores (alta y listado)
Función MenuDoctores (ids Por Referencia,nombres Por Referencia,especialidades Por Referencia,cantidad Por Referencia)
	Definir opc, i Como Entero
	Definir nuevoId, nuevoNombre, nuevaEspecialidad, respuesta Como Cadena
	Definir continuar, idValido Como Lógico
	opc <- 0
	Mientras opc<>3 Hacer
		Limpiar Pantalla
		Escribir '========== MODULO DOCTORES =========='
		Escribir '1. Listar doctores'
		Escribir '2. Registrar doctor'
		Escribir '3. Regresar al menu principal'
		Escribir 'Seleccione una opcion: '
		Leer opc
		Según opc Hacer
			1:
				Limpiar Pantalla
				Escribir '--- LISTA DE DOCTORES ---'
				Si cantidad=0 Entonces
					Escribir 'No hay doctores registrados.'
				SiNo
					Para i<-1 Hasta cantidad Hacer
						Escribir ids[i]+' | '+nombres[i]+' | '+especialidades[i]
					FinPara
				FinSi
				Escribir ''
				Escribir 'Presione una tecla para continuar...'
				Esperar Tecla
			2:
				continuar <- Verdadero
				Mientras continuar Hacer
					Limpiar Pantalla
					// Validar ID unico (no vacio y no duplicado)
					idValido <- Falso
					Mientras idValido=Falso Hacer
						Escribir 'Ingrese ID unico del doctor: '
						Leer nuevoId
						Si nuevoId='' Entonces
							Escribir 'Error: el ID no puede estar vacio.'
						SiNo
							Si BuscarIndice(ids,cantidad,nuevoId)<>-1 Entonces
								Escribir 'Error: ID duplicado. Intente con otro.'
							SiNo
								idValido <- Verdadero
							FinSi
						FinSi
					FinMientras
					Escribir 'Ingrese nombre completo: '
					Leer nuevoNombre
					Escribir 'Ingrese especialidad: '
					Leer nuevaEspecialidad
					cantidad <- cantidad+1
					ids[cantidad] <- nuevoId
					nombres[cantidad] <- nuevoNombre
					especialidades[cantidad] <- nuevaEspecialidad
					Escribir ''
					Escribir 'Doctor registrado exitosamente: '+nuevoId+' | '+nuevoNombre+' | '+nuevaEspecialidad
					Escribir ''
					Escribir 'Registrar otro doctor? (S/N): '
					Leer respuesta
					Si respuesta<>'S' Y respuesta<>'s' Entonces
						continuar <- Falso
					FinSi
				FinMientras
			3:
				// Regresa al menu principal
			De Otro Modo:
				Escribir 'Opcion no valida.'
				Esperar Tecla
		FinSegún
	FinMientras
FinFunción

// --------- SubProceso: modulo de pacientes (alta y listado)
Función MenuPacientes (ids Por Referencia,nombres Por Referencia,cantidad Por Referencia)
	Definir opc, i Como Entero
	Definir nuevoId, nuevoNombre, respuesta Como Cadena
	Definir continuar, idValido Como Lógico
	opc <- 0
	Mientras opc<>3 Hacer
		Limpiar Pantalla
		Escribir '========== MODULO PACIENTES =========='
		Escribir '1. Listar pacientes'
		Escribir '2. Registrar paciente'
		Escribir '3. Regresar al menu principal'
		Escribir 'Seleccione una opcion: '
		Leer opc
		Según opc Hacer
			1:
				Limpiar Pantalla
				Escribir '--- LISTA DE PACIENTES ---'
				Si cantidad=0 Entonces
					Escribir 'No hay pacientes registrados.'
				SiNo
					Para i<-1 Hasta cantidad Hacer
						Escribir ids[i]+' | '+nombres[i]
					FinPara
				FinSi
				Escribir ''
				Escribir 'Presione una tecla para continuar...'
				Esperar Tecla
			2:
				continuar <- Verdadero
				Mientras continuar Hacer
					Limpiar Pantalla
					idValido <- Falso
					Mientras idValido=Falso Hacer
						Escribir 'Ingrese ID unico del paciente: '
						Leer nuevoId
						Si nuevoId='' Entonces
							Escribir 'Error: el ID no puede estar vacio.'
						SiNo
							Si BuscarIndice(ids,cantidad,nuevoId)<>-1 Entonces
								Escribir 'Error: ID duplicado. Intente con otro.'
							SiNo
								idValido <- Verdadero
							FinSi
						FinSi
					FinMientras
					Escribir 'Ingrese nombre completo: '
					Leer nuevoNombre
					cantidad <- cantidad+1
					ids[cantidad] <- nuevoId
					nombres[cantidad] <- nuevoNombre
					Escribir ''
					Escribir 'Paciente registrado exitosamente: '+nuevoId+' | '+nuevoNombre
					Escribir ''
					Escribir 'Registrar otro paciente? (S/N): '
					Leer respuesta
					Si respuesta<>'S' Y respuesta<>'s' Entonces
						continuar <- Falso
					FinSi
				FinMientras
			3:
				// Regresa al menu principal
			De Otro Modo:
				Escribir 'Opcion no valida.'
				Esperar Tecla
		FinSegún
	FinMientras
FinFunción

// --------- SubProceso: modulo de citas (crear y listar)
Función MenuCitas (idsC Por Referencia,fechasC Por Referencia,motivosC Por Referencia,docsC Por Referencia,pacsC Por Referencia,cantC Por Referencia,idsD,nomsD,cantD,idsP,nomsP,cantP)
	Definir opc, i, indiceDoc, indicePac Como Entero
	Definir nuevoId, nuevaFecha, nuevoMotivo, idDocSel, idPacSel, respuesta Como Cadena
	Definir continuar, idValido Como Lógico
	opc <- 0
	Mientras opc<>3 Hacer
		Limpiar Pantalla
		Escribir '========== MODULO CITAS =========='
		Escribir '1. Listar citas'
		Escribir '2. Crear cita'
		Escribir '3. Regresar al menu principal'
		Escribir 'Seleccione una opcion: '
		Leer opc
		Según opc Hacer
			1:
				Limpiar Pantalla
				Escribir '--- LISTA DE CITAS ---'
				Si cantC=0 Entonces
					Escribir 'No hay citas registradas.'
				SiNo
					Para i<-1 Hasta cantC Hacer
						Escribir idsC[i]+' | '+fechasC[i]+' | '+motivosC[i]+' | Dr: '+docsC[i]+' | Pac: '+pacsC[i]
					FinPara
				FinSi
				Escribir ''
				Escribir 'Presione una tecla para continuar...'
				Esperar Tecla
			2:
				Limpiar Pantalla
				Si cantD=0 Entonces
					Escribir 'Error: No hay doctores registrados. Registre un doctor primero.'
					Esperar Tecla
				SiNo
					Si cantP=0 Entonces
						Escribir 'Error: No hay pacientes registrados. Registre un paciente primero.'
						Esperar Tecla
					SiNo
						continuar <- Verdadero
						Mientras continuar Hacer
							Limpiar Pantalla
							idValido <- Falso
							Mientras idValido=Falso Hacer
								Escribir 'Ingrese ID unico de la cita: '
								Leer nuevoId
								Si nuevoId='' Entonces
									Escribir 'Error: el ID no puede estar vacio.'
								SiNo
									Si BuscarIndice(idsC,cantC,nuevoId)<>-1 Entonces
										Escribir 'Error: ID duplicado. Intente con otro.'
									SiNo
										idValido <- Verdadero
									FinSi
								FinSi
							FinMientras
							Escribir 'Ingrese fecha y hora (DD/MM/AAAA HH:MM): '
							Leer nuevaFecha
							Escribir 'Ingrese motivo de la cita: '
							Leer nuevoMotivo
							Escribir ''
							Escribir '--- DOCTORES DISPONIBLES ---'
							Para i<-1 Hasta cantD Hacer
								Escribir idsD[i]+' | '+nomsD[i]
							FinPara
							indiceDoc <- -1
							Mientras indiceDoc=-1 Hacer
								Escribir 'Ingrese ID del doctor: '
								Leer idDocSel
								indiceDoc <- BuscarIndice(idsD,cantD,idDocSel)
								Si indiceDoc=-1 Entonces
									Escribir 'Error: Doctor no encontrado. Intente de nuevo.'
								FinSi
							FinMientras
							Escribir ''
							Escribir '--- PACIENTES REGISTRADOS ---'
							Para i<-1 Hasta cantP Hacer
								Escribir idsP[i]+' | '+nomsP[i]
							FinPara
							indicePac <- -1
							Mientras indicePac=-1 Hacer
								Escribir 'Ingrese ID del paciente: '
								Leer idPacSel
								indicePac <- BuscarIndice(idsP,cantP,idPacSel)
								Si indicePac=-1 Entonces
									Escribir 'Error: Paciente no encontrado. Intente de nuevo.'
								FinSi
							FinMientras
							cantC <- cantC+1
							idsC[cantC] <- nuevoId
							fechasC[cantC] <- nuevaFecha
							motivosC[cantC] <- nuevoMotivo
							docsC[cantC] <- idDocSel
							pacsC[cantC] <- idPacSel
							Escribir ''
							Escribir 'Cita creada exitosamente: '+nuevoId+' | '+nuevaFecha+' | '+nuevoMotivo
							Escribir 'Doctor: '+idDocSel+' | Paciente: '+idPacSel
							Escribir ''
							Escribir 'Crear otra cita? (S/N): '
							Leer respuesta
							Si respuesta<>'S' Y respuesta<>'s' Entonces
								continuar <- Falso
							FinSi
						FinMientras
					FinSi
				FinSi
			3:
				// Regresa al menu principal
			De Otro Modo:
				Escribir 'Opcion no valida.'
				Esperar Tecla
		FinSegún
	FinMientras
FinFunción

// =============================================================
// ALGORITMO PRINCIPAL
// =============================================================
Algoritmo SistemaClinica
	Dimensionar idsDoctores(100), nombresDoctores(100), especialidadesDoctores(100)
	Definir idsDoctores, nombresDoctores, especialidadesDoctores Como Cadena
	Dimensionar idsPacientes(100), nombresPacientes(100)
	Definir idsPacientes, nombresPacientes Como Cadena
	Dimensionar idsCitas(100), fechasCitas(100), motivosCitas(100), doctoresCita(100), pacientesCita(100)
	Definir idsCitas, fechasCitas, motivosCitas, doctoresCita, pacientesCita Como Cadena
	Dimensionar idAdmins(10), passAdmins(10)
	Definir idAdmins, passAdmins Como Cadena
	Definir cantDoctores, cantPacientes, cantCitas, cantAdmins Como Entero
	Definir opcionMenu, i Como Entero
	Definir idIngresado, passIngresada Como Cadena
	Definir autenticado Como Lógico
	cantDoctores <- 0
	cantPacientes <- 0
	cantCitas <- 0
	cantAdmins <- 1
	idAdmins[1] <- 'admin'
	passAdmins[1] <- '1234'
	// ----- LOGIN (control de acceso)
	autenticado <- Falso
	Repetir
		Limpiar Pantalla
		Escribir '=== LOGIN DEL SISTEMA ==='
		Escribir 'ID de administrador: '
		Leer idIngresado
		Escribir 'Contraseña: '
		Leer passIngresada
		Para i<-1 Hasta cantAdmins Hacer
			Si idIngresado=idAdmins[i] Y passIngresada=passAdmins[i] Entonces
				autenticado <- Verdadero
			FinSi
		FinPara
		Si autenticado=Falso Entonces
			Escribir 'Error: Acceso denegado. Presione Enter para Intentar de nuevo.'
			Esperar Tecla
		FinSi
	Hasta Que autenticado=Verdadero
	// ----- MENU PRINCIPAL
	opcionMenu <- 0
	Mientras opcionMenu<>4 Hacer
		Limpiar Pantalla
		Escribir '=== MENU PRINCIPAL ==='
		Escribir '1. Gestionar Doctores'
		Escribir '2. Gestionar Pacientes'
		Escribir '3. Gestionar Citas'
		Escribir '4. Salir'
		Escribir 'Seleccione una opcion: '
		Leer opcionMenu
		Según opcionMenu Hacer
			1:
				MenuDoctores(idsDoctores,nombresDoctores,especialidadesDoctores,cantDoctores)
			2:
				MenuPacientes(idsPacientes,nombresPacientes,cantPacientes)
			3:
				MenuCitas(idsCitas,fechasCitas,motivosCitas,doctoresCita,pacientesCita,cantCitas,idsDoctores,nombresDoctores,cantDoctores,idsPacientes,nombresPacientes,cantPacientes)
			4:
				Limpiar Pantalla
				Escribir 'Sesion cerrada.'
				Escribir 'Guardando informacion...'
			De Otro Modo:
				Escribir 'Opcion no valida.'
				Esperar Tecla
		FinSegún
	FinMientras
	Escribir 'Programa finalizado.'
FinAlgoritmo
