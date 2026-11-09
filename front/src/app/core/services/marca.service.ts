
import { Injectable, inject } from '@angular/core';
import {
  HttpClient,
  HttpParams
} from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';

import {
  Marca,
  MarcaCadastro
} from '../models/marca.model';

@Injectable({
  providedIn: 'root'
})
export class MarcaService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl =
    `${environment.apiUrl}/marcas`;

  inserir(marca: MarcaCadastro): Observable<Marca> {

    const dados: MarcaCadastro = {
      nome: marca.nome.trim(),
      cnpj: marca.cnpj.trim()
    };

    return this.http.post<Marca>(
      this.apiUrl,
      dados
    );
  }

  listar(filtro: string = ''): Observable<Marca[]> {

    const params = new HttpParams()
      .set('filtro', filtro.trim());

    return this.http.get<Marca[]>(
      this.apiUrl,
      { params }
    );
  }
}
