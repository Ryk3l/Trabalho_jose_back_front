import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';

import { Marca } from '../../core/models/marca.model';
import { MarcaService } from '../../core/services/marca.service';

/** Lista o top 10 alfabético de marcas, com filtro por nome (GET /api/marcas?filtro=...). */
@Component({
  imports: [FormsModule, InputTextModule, ButtonModule],
  selector: 'app-marca-lista',
  templateUrl: './marca-lista.component.html',
})
export class MarcaListaComponent {

  private readonly marcaService = inject(MarcaService);
  private readonly toast = inject(MessageService);

  protected filtro = '';
  protected marcas = signal<Marca[]>([]);
  protected carregando = signal(false);

  ngOnInit(): void {
    this.atualizar();
  }

  /** Busca a listagem no service; exposto para o formulário recarregar após salvar. */
  atualizar(): void {
    this.carregando.set(true);

    this.marcaService.listar(this.filtro).subscribe({
      next: (marcas) => {
        this.marcas.set(marcas);
        this.carregando.set(false);
      },
      error: () => {
        this.carregando.set(false);
        this.toast.add({
          severity: 'error',
          summary: 'Erro na listagem',
          detail: 'Não foi possível carregar as marcas. Verifique se o backend está rodando.',
          life: 5000,
        });
      },
    });
  }
}
