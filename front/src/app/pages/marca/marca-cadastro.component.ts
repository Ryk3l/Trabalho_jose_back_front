import { Component, inject, signal, viewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { ToastModule } from 'primeng/toast';

import { MarcaCadastro } from '../../core/models/marca.model';
import { MarcaService } from '../../core/services/marca.service';
import { MarcaListaComponent } from './marca-lista.component';

/** Aceita CNPJ com ou sem máscara: 14 dígitos, com/sem pontos, barra e hífen. */
const CNPJ_PATTERN = /^\d{2}\.?\d{3}\.?\d{3}\/?\d{4}-?\d{2}$/;

@Component({
  imports: [
    ReactiveFormsModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    MarcaListaComponent,
  ],
  selector: 'app-marca-cadastro',
  templateUrl: './marca-cadastro.component.html',
})
export class MarcaCadastroComponent {

  private readonly formBuilder = inject(FormBuilder);
  private readonly marcaService = inject(MarcaService);
  private readonly toast = inject(MessageService);

  /** Lista exibida logo abaixo do formulário; é recarregada após cada cadastro. */
  private readonly lista = viewChild.required(MarcaListaComponent);

  /** Bloqueia o botão enquanto a requisição está em andamento. */
  protected readonly enviando = signal(false);

  protected readonly form = this.formBuilder.nonNullable.group({
    // Espelho das regras do MarcaRequest no backend (obrigatório, máximo 150).
    nome: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(150)]],
    // Espelho do MarcaRequest (obrigatório, máximo 18) + formato de CNPJ.
    cnpj: ['', [Validators.required, Validators.maxLength(18), Validators.pattern(CNPJ_PATTERN)]],
  });

  protected enviar(): void {
    // Formulário inválido não tem envio (o botão já nasce desabilitado; aqui é a dupla garantia).
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.enviando.set(true);

    this.marcaService.inserir(this.form.getRawValue()).subscribe({
      next: (marca) => {
        this.enviando.set(false);
        this.toast.add({
          severity: 'success',
          summary: 'Marca cadastrada',
          detail: `"${marca.nome}" foi salva com sucesso.`,
          life: 4000,
        });
        this.form.reset();
        this.lista().atualizar();
      },
      error: (erro) => {
        this.enviando.set(false);
        this.toast.add(this.traduzirErro(erro));
      },
    });
  }

  /** Converte as respostas de erro do backend em mensagem de Toast (nada é silenciado). */
  private traduzirErro(erro: unknown): { severity: string; summary: string; detail: string; life: number } {
    const corpo = (erro as { error?: unknown })?.error;

    // CNPJ duplicado: { "mensagem": "Ja existe..." }
    if (corpo && typeof corpo === 'object' && typeof (corpo as { mensagem?: unknown }).mensagem === 'string') {
      return {
        severity: 'warn',
        summary: 'Cadastro recusado',
        detail: (corpo as { mensagem: string }).mensagem,
        life: 5000,
      };
    }

    // Erro de validação: { "nome": "...", "cnpj": "..." }
    if (corpo && typeof corpo === 'object') {
      const rotulos: Record<string, string> = { nome: 'Nome', cnpj: 'CNPJ' };
      const detalhes = Object.entries(corpo as Record<string, string>)
        .map(([campo, mensagem]) => `${rotulos[campo] ?? campo}: ${mensagem}`)
        .join(' ');
      if (detalhes) {
        return { severity: 'error', summary: 'Dados inválidos', detail: detalhes, life: 5000 };
      }
    }

    // Backend fora do ar ou resposta sem corpo conhecido.
    return {
      severity: 'error',
      summary: 'Erro no cadastro',
      detail: 'Não foi possível falar com o servidor. Verifique se o backend está rodando.',
      life: 5000,
    };
  }

  protected erroNome(): string | null {
    return this.mensagemErro(this.form.controls.nome, {
      required: 'O nome é obrigatório.',
      minlength: 'O nome deve ter no mínimo 2 caracteres.',
      maxlength: 'O nome deve ter no máximo 150 caracteres.',
    });
  }

  protected erroCnpj(): string | null {
    return this.mensagemErro(this.form.controls.cnpj, {
      required: 'O CNPJ é obrigatório.',
      maxlength: 'O CNPJ deve ter no máximo 18 caracteres.',
      pattern: 'Informe um CNPJ válido com 14 dígitos (com ou sem máscara).',
    });
  }

  private mensagemErro(control: { invalid: boolean; touched: boolean; errors: Record<string, unknown> | null }, mensagens: Record<string, string>): string | null {
    if (!control.invalid || !control.touched) {
      return null;
    }
    const erro = Object.keys(control.errors ?? {})[0];
    return erro ? mensagens[erro] ?? 'Valor inválido.' : null;
  }
}
