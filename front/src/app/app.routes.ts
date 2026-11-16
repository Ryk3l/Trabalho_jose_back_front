import { Routes } from '@angular/router';
import { MarcaCadastroComponent } from './pages/marca/marca-cadastro.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'marcas' },
  { path: 'marcas', component: MarcaCadastroComponent, title: 'Cadastro de Marcas' },
  { path: '**', redirectTo: 'marcas' },
];
